const admin = require("firebase-admin");
const fs = require("fs");

const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Read the JSON file and parse the data
const usersData = JSON.parse(fs.readFileSync("json/users.json", "utf8"));

// Access the `users` array inside the JSON structure
const usersArray = usersData.users;

if (Array.isArray(usersArray)) {
    usersArray.forEach((user) => {
        db.collection("users").doc(user.email).set(user)
            .then(() => {
                console.log(`User ${user.email} added successfully.`);
            })
            .catch((error) => {
                console.error("Error adding user:", error);
            });
    });
} else {
    console.error("Expected an array of users in JSON file.");
}
