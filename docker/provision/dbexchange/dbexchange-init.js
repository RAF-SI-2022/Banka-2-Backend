/**
 * Exchange database (MongoDB) initialization shell script (JS).
 * Reference: https://www.mongodb.com/docs/manual/reference/method/
 */

// prod
db = db.getSiblingDb('exchange-prod')
db.createCollection('accounts')

// test
db = db.getSiblingDb('exchange-test')
db.dropDatabase()
db = db.getSiblingDb('exchange-test')
db.createCollection('tests')

// dev
db = db.getSiblingDb('exchange-dev')
db.createCollection('forexes')

// user
db.createUser(
    {
        user: "root",
        pwd: "root",
        roles: [
            {
                role: "readWrite",
                db: "exchange-prod"
            },
            {
                role: "readWrite",
                db: "exchange-test"
            },
            {
                role: "readWrite",
                db: "exchange-dev"
            },
        ]
    }
);