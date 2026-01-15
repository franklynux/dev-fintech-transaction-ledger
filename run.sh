#!/bin/bash

echo "🚀 Starting Transaction Ledger..."

# Stop any running containers
docker-compose down

# Build and start
docker-compose build --no-cache
docker-compose up -d

echo "⏳ Waiting for services to start (30 seconds)..."
sleep 30

echo "✅ Checking service status..."
curl -f http://localhost:8080/actuator/health || echo "❌ Service not ready yet"

echo ""
echo "📋 Service URLs:"
echo "   Application: http://localhost:8080"
echo "   Health:      http://localhost:8080/actuator/health"
echo "   DB Health:   http://localhost:8080/api/health/database"
echo ""
echo "📝 View logs: docker-compose logs -f"
