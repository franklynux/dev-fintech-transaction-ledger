#!/bin/bash

# Build script for Transaction Ledger

echo "🔨 Building Transaction Ledger..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Clean and build
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Maven build successful"
else
    echo "❌ Maven build failed"
    exit 1
fi

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t transaction-ledger:latest .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully"
    echo "📦 Image: transaction-ledger:latest"
else
    echo "❌ Docker build failed"
    exit 1
fi

echo "🎉 Build completed successfully!"
