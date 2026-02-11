#!/bin/bash

# =====================================================
# LogiFlow - Kubernetes Deployment Script (Linux/Mac)
# =====================================================

echo "=========================================="
echo "  LogiFlow - Kubernetes Deployment"
echo "=========================================="

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Check kubectl
echo -e "\n${YELLOW}Verifying kubectl...${NC}"
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}ERROR - kubectl is not installed${NC}"
    exit 1
fi
echo -e "${GREEN}OK - kubectl is ready${NC}"

# Step 1: Create namespace
echo -e "\n${YELLOW}[1/7] Creating namespace...${NC}"
kubectl apply -f k8s/namespace.yaml
[ $? -eq 0 ] && echo -e "${GREEN}OK - Namespace created${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

# Step 2: Create ConfigMap and Secrets
echo -e "\n${YELLOW}[2/7] Creating ConfigMap and Secrets...${NC}"
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
[ $? -eq 0 ] && echo -e "${GREEN}OK - ConfigMap and Secrets created${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

# Step 3: Create PVCs
echo -e "\n${YELLOW}[3/7] Creating Persistent Volume Claims...${NC}"
kubectl apply -f k8s/storage/
[ $? -eq 0 ] && echo -e "${GREEN}OK - PVCs created${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

# Step 4: Deploy Databases
echo -e "\n${YELLOW}[4/7] Deploying databases...${NC}"
kubectl apply -f k8s/databases/
[ $? -eq 0 ] && echo -e "${GREEN}OK - Databases deployed${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

echo -e "\n${YELLOW}Waiting for databases to be ready (60 seconds)...${NC}"
sleep 60

# Step 5: Deploy Infrastructure
echo -e "\n${YELLOW}[5/7] Deploying infrastructure...${NC}"
kubectl apply -f k8s/infrastructure/
[ $? -eq 0 ] && echo -e "${GREEN}OK - Infrastructure deployed${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

echo -e "\n${YELLOW}Waiting for RabbitMQ to be ready (30 seconds)...${NC}"
sleep 30

# Step 6: Deploy Microservices
echo -e "\n${YELLOW}[6/7] Deploying microservices...${NC}"
kubectl apply -f k8s/services/
[ $? -eq 0 ] && echo -e "${GREEN}OK - Microservices deployed${NC}" || { echo -e "${RED}ERROR - Failed${NC}"; exit 1; }

# Step 7: Deploy Ingress
echo -e "\n${YELLOW}[7/7] Deploying Ingress...${NC}"
kubectl apply -f k8s/ingress.yaml
[ $? -eq 0 ] && echo -e "${GREEN}OK - Ingress deployed${NC}" || echo -e "${YELLOW}WARNING - Ingress failed${NC}"

# Summary
echo -e "\n${GREEN}=========================================="
echo -e "  Deployment Complete!"
echo -e "==========================================${NC}"

echo -e "\n${YELLOW}Verifying deployment status...${NC}"
kubectl get pods -n logiflow

echo -e "\nNext steps:"
echo -e "  1. Check pods: ${YELLOW}kubectl get pods -n logiflow -w${NC}"
echo -e "  2. Check services: ${YELLOW}kubectl get svc -n logiflow${NC}"
echo -e "  3. View logs: ${YELLOW}kubectl logs -f deployment/api-gateway -n logiflow${NC}"
echo -e "  4. Port forward: ${YELLOW}kubectl port-forward svc/api-gateway 8080:8080 -n logiflow${NC}"
