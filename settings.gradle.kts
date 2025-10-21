rootProject.name = "springboot-microservices"

include(
    "gateway-service",
    "user-service",
    "order-service",
    "inventory-service",
    "payment-service",
    "notification-service",
    "stream-processor"
)