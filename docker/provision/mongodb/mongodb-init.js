/**
 * MongoDB initialization shell script (JS).
 * Reference: https://www.mongodb.com/docs/manual/reference/method/
 */

// prod
db = db.getSiblingDb('prod')
db.createCollection('accounts')

// test
db = db.getSiblingDb('test')
db.dropDatabase()
db = db.getSiblingDb('test')
db.createCollection('tests')

// dev
db = db.getSiblingDb('dev')

// user
db.createUser(
    {
        user: "root",
        pwd: "root",
        roles: [
            {
                role: "readWrite",
                db: "prod"
            },
            {
                role: "readWrite",
                db: "test"
            },
            {
                role: "readWrite",
                db: "dev"
            },
        ]
    }
);