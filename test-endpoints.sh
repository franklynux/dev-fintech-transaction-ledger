#!/bin/bash

# Test endpoints for Transaction Ledger

echo "🧪 Testing Transaction Ledger endpoints..."
echo "=========================================="

# Wait for service to be ready
echo "⏳ Waiting for service to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null; then
        echo "✅ Service is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ Service did not start in time"
        exit 1
    fi
    sleep 2
done

echo ""
echo "1️⃣  Testing service health:"
curl -s http://localhost:8080/api/health/service | jq '.'

echo ""
echo "2️⃣  Testing database connection:"
curl -s http://localhost:8080/api/health/database | jq '.'

echo ""
echo "3️⃣  Testing full health check:"
curl -s http://localhost:8080/api/health/full | jq '.'

echo ""
echo "4️⃣  Testing PCI logging demo:"
curl -s http://localhost:8080/api/test/log-demo | jq '.'

echo ""
echo "5️⃣  Testing configuration:"
curl -s http://localhost:8080/api/test/config | jq '.'

echo ""
echo "6️⃣  Testing transaction with PCI data:"
curl -s -X POST http://localhost:8080/api/test/transaction \
  -H "Content-Type: application/json" \
  -d '{
    "cardNumber": "4111111111111111",
    "cardHolderName": "John Doe",
    "amount": 99.99,
    "currency": "USD",
    "transactionType": "PURCHASE"
  }' | jq '.'

echo ""
echo "7️⃣  Actuator health endpoint:"
curl -s http://localhost:8080/actuator/health | jq '.'

echo ""
echo "=========================================="
echo "🎉 All tests completed!"
echo ""
echo "�� Quick Reference:"
echo "   Service URL: http://localhost:8080"
echo "   Health Check: http://localhost:8080/api/health/full"
echo "   PCI Demo: http://localhost:8080/api/test/log-demo"
echo "   Database: localhost:5432"
echo ""
echo "📝 Check application logs for PCI redaction:"
echo "   docker-compose logs transaction-ledger"
