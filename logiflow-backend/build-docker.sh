#!/bin/bash

# =====================================================
# LogiFlow - Build Script for Docker Images
# =====================================================

echo "=========================================="
echo "  LogiFlow - Building Docker Images"
echo "=========================================="

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Build common module first
echo -e "${YELLOW}Building common module...${NC}"
cd common
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Common module built successfully${NC}"
else
    echo -e "${RED}✗ Failed to build common module${NC}"
    exit 1
fi
cd ..

# Services to build
services=("auth-service" "pedido-service" "fleet-service" "billing-service" "graphql-service" "notification-service" "api-gateway")

# Build each service JAR
for service in "${services[@]}"; do
    echo -e "\n${YELLOW}Building $service...${NC}"
    cd $service
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $service built successfully${NC}"
    else
        echo -e "${RED}✗ Failed to build $service${NC}"
        exit 1
    fi
    cd ..
done

# Build Docker images
echo -e "\n${YELLOW}Building Docker images...${NC}"
docker-compose -f docker-compose-full.yml build

if [ $? -eq 0 ]; then
    echo -e "\n${GREEN}=========================================="
    echo -e "  All services built successfully!"
    echo -e "==========================================${NC}"
    echo -e "\nNext steps:"
    echo -e "  1. Run: ${YELLOW}docker-compose -f docker-compose-full.yml up -d${NC}"
    echo -e "  2. Check logs: ${YELLOW}docker-compose -f docker-compose-full.yml logs -f${NC}"
    echo -e "  3. Stop: ${YELLOW}docker-compose -f docker-compose-full.yml down${NC}"
else
    echo -e "${RED}✗ Failed to build Docker images${NC}"
    exit 1
fi
