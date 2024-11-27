const admin = require("firebase-admin");
const fs = require("fs");

// Initialize Firebase Admin with service account key
const serviceAccount = require("./serviceAccountKey.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

// Read departments.json file
const departmentsData = JSON.parse(fs.readFileSync("./json/departments.json", "utf8"));

async function importDepartments() {
  for (const doc of departmentsData.documents) {
    const docRef = db.collection("departments").doc(doc.name.split("/")[1]);
    await docRef.set({
      departmentName: doc.fields.departmentName.stringValue,
      sections: doc.fields.sections.arrayValue.values.map(value => value.stringValue)
    });
    console.log(`Imported ${doc.fields.departmentName.stringValue}`);
  }
}

importDepartments()
  .then(() => {
    console.log("Departments import completed successfully.");
    process.exit();
  })
  .catch((error) => {
    console.error("Error importing departments:", error);
    process.exit(1);
  });
