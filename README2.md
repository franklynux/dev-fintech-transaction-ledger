##########################################################
Build and Run the Application

Option A: Using the scripts (Recommended)
Build the application:
##########################################################


#Make Scripts Executable

chmod +x build.sh run.sh test-endpoints.sh


Build the application:

./build.sh

Run the application:

./run.sh

Test the endpoints:

./test-endpoints.sh


##############################################
Build and Run the Application
Option B: Manual steps
##############################################


Build with Maven:

mvn clean package -DskipTests

Build Docker image:

docker build -t transaction-ledger .

Start with Docker Compose:

docker-compose up  --build


Test in another terminal:

# Check if service is running
curl http://localhost:8080/actuator/health

# Test database connection
curl http://localhost:8080/api/health/database

# Test PCI logging

curl http://localhost:8080/api/test/log-demo

#########################################################

Verify Implementation

##########################################################

Verification Checklist:
✅ Container runs successfully:


docker ps
# Should show both transaction-ledger and transaction-postgres containers
✅ Application starts without errors:


docker-compose logs transaction-ledger
# Look for "Started TransactionLedgerApplication" and no errors
✅ Database connection works:


curl http://localhost:8080/api/health/database
# Should return {"status":"UP","connected":true}
✅ PCI logging works:


curl http://localhost:8080/api/test/log-demo
# Check logs: docker-compose logs transaction-ledger | grep -i redact
# Should show "[CARD_REDACTED]" instead of actual card numbers
✅ External DB connection (for production):
To connect to an external PostgreSQL database instead of the Docker one:


# Stop the PostgreSQL container
docker-compose stop postgres-db

# Set environment variables
export DB_HOST=your-external-db.com
export DB_PORT=5432
export DB_NAME=your_database
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

# Start only the application
docker-compose up transaction-ledger
✅ Check logs for PCI compliance:


# Generate some test transactions
curl -X POST http://localhost:8080/api/test/transaction \
-H "Content-Type: application/json" \
-d '{"cardNumber":"5555555555554444","cardHolderName":"Test User","amount":50.00}'

# Check logs
docker-compose logs transaction-ledger --tail=20
# Should NOT show full card numbers

###################################################
Troubleshooting
###################################################

Common Issues:
Port already in use:


# Check what's using port 8080
sudo lsof -i :8080
# Or use a different port in docker-compose.yml
Database connection refused:


# Check if PostgreSQL is running
docker-compose logs postgres-db

# Wait longer for database to initialize
# Increase health check start period in docker-compose.yml
PCI logging not working:


# Check if redaction is enabled
curl http://localhost:8080/api/test/config

# Enable it in .env file
PCI_LOG_REDACTION_ENABLED=true

Build fails:


# Clean and rebuild
mvn clean
docker system prune -a
./build.sh
Quick Reference Commands
bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f transaction-ledger

# View PostgreSQL logs
docker-compose logs -f postgres-db

# Check container status
docker-compose ps

# Execute command in container
docker-compose exec transaction-ledger sh

# View database
docker-compose exec postgres-db psql -U transaction_user -d transaction_ledger

# Rebuild and restart
docker-compose up --build -d

# Remove everything (including volumes)
docker-compose down -v

###################################################
Testing with External Database
###################################################

To test with an external PostgreSQL database (not the Docker one):

bash
# Create a .env file with external DB details
cat > .env << EOF
DB_HOST=your-external-db-host.com
DB_PORT=5432
DB_NAME=transaction_db
DB_USERNAME=db_user
DB_PASSWORD=db_password
SPRING_PROFILES_ACTIVE=docker
PCI_LOG_REDACTION_ENABLED=true
EOF

# Start only the application (without PostgreSQL container)
docker-compose up transaction-ledger
