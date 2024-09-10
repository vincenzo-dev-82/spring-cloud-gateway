아래는 Markdown 형식으로 변환한 내용입니다.

```markdown
# Spring Cloud Gateway (Spring Boot 3 기준)

Spring Cloud Gateway는 Spring Boot 애플리케이션을 위한 API 게이트웨이 솔루션으로, 클라이언트 요청을 다양한 서비스로 라우팅하고 필터링, 인증, 로깅 등의 기능을 제공합니다.

## 주요 기능
1. **라우팅(Routing)**: 클라이언트 요청을 특정 서비스로 라우팅.
2. **필터링(Filter)**: 요청/응답을 가로채 추가 처리(예: 인증, 로깅).
3. **로드 밸런싱(Load Balancing)**: Spring Cloud LoadBalancer와 통합.
4. **보안(Security)**: 인증 및 권한 부여.
5. **성능 향상**: 게이트웨이 레벨에서 캐싱, 압축 적용.

---

## Spring Cloud Gateway 설정 및 구현

### 1. 의존성 설정

Spring Boot 3 프로젝트에서 Spring Cloud Gateway를 사용하려면, `spring-cloud-starter-gateway` 의존성을 추가합니다.

```gradle
extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-gateway")
}
```

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2022.0.4</version> <!-- Spring Cloud 버전 -->
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

### 2. 기본 라우팅 설정

`application.yml` 파일에서 기본적인 라우팅을 설정할 수 있습니다.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: my-service
          uri: http://localhost:8081
          predicates:
            - Path=/service/**
          filters:
            - StripPrefix=1
```

#### 설정 설명:
- **`id`**: 라우트의 고유 식별자.
- **`uri`**: 라우팅할 서비스의 URI.
- **`predicates`**: 요청 경로 조건 설정.
- **`filters`**: 요청/응답을 필터링하는 작업.

---

### 3. 라우팅 필터 적용

요청 전/후에 헤더를 추가하거나, 인증을 수행할 수 있습니다.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: add-header-route
          uri: http://localhost:8081
          predicates:
            - Path=/api/**
          filters:
            - AddRequestHeader=X-Request-Foo, Bar
            - AddResponseHeader=X-Response-Foo, Bar
```

#### 설명:
- `AddRequestHeader`: 요청 헤더를 추가.
- `AddResponseHeader`: 응답 헤더를 추가.

---

### 4. Custom Filter 구현

필터를 직접 구현하여 커스텀 로직을 추가할 수 있습니다.

```java
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

    public CustomGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Custom pre-filter logic here
            System.out.println("Custom pre-filter: " + exchange.getRequest().getURI());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Custom post-filter logic here
                System.out.println("Custom post-filter: " + exchange.getResponse().getStatusCode());
            }));
        };
    }

    public static class Config {
        // Configuration properties here
    }
}
```

`application.yml`에서 이 필터를 사용할 수 있습니다.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: custom-filter-route
          uri: http://localhost:8082
          predicates:
            - Path=/custom/**
          filters:
            - name: CustomGatewayFilterFactory
```

---

### 5. 로드 밸런싱

`Spring Cloud LoadBalancer`를 사용하여 여러 인스턴스 간에 트래픽을 분산할 수 있습니다.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: load-balanced-service
          uri: lb://my-service
          predicates:
            - Path=/lb/**
```

이 설정에서 `lb://my-service`는 로드 밸런싱된 서비스 URI입니다.

---

### 6. Spring Security와 연동

Spring Cloud Gateway는 Spring Security와 연동하여 인증 및 권한 부여를 처리할 수 있습니다.

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://issuer.example.com
```

그리고 `WebSecurityConfigurerAdapter` 또는 Security 설정 클래스에서 이를 정의할 수 있습니다.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/public/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
        return http.build();
    }
}
```

이 코드는 JWT 기반 인증을 사용하고, `/public/**` 경로는 인증 없이 접근할 수 있도록 설정합니다.

---

## 결론

Spring Cloud Gateway는 Spring Boot 3와 함께 API 게이트웨이를 손쉽게 구축하고 확장할 수 있는 솔루션입니다. 기본적인 라우팅 설정부터 커스텀 필터 구현, 로드 밸런싱, Spring Security와의 연동까지 다양한 기능을 제공하여 마이크로서비스 아키텍처에서 API 관리를 효율적으로 할 수 있습니다.
```
