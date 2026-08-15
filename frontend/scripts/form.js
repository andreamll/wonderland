// ===============================
// DOCUMENT ROW MANAGEMENT
// ===============================

// Handles the "Add Another Document" button
document.addEventListener("DOMContentLoaded", () => {

    const container = document.getElementById("documentsContainer");
    const addButton = document.getElementById("addDocumentBtn");
  
    // Add a new document row
    addButton.addEventListener("click", () => {
  
      const newRow = document.createElement("div");
      newRow.className = "upload-row";
  
      newRow.innerHTML = `
        <div class="field">
          <label>Document Type</label>
          <select name="documentType[]">
            <option selected disabled>Select a document</option>
            <option>Passport Copy</option>
            <option>Income Proof</option>
            <option>Letter of Acceptance</option>
            <option>Explanation Letter</option>
            <option>Employment Offer</option>
          </select>
        </div>
  
        <div class="field">
          <label>Upload File</label>
          <input type="file" name="documentFile[]" />
        </div>
  
        <button class="ghost-btn remove-doc-btn" type="button">Remove</button>
      `;
  
      container.appendChild(newRow);
  
    });
  
    // Remove document row
    container.addEventListener("click", (e) => {
  
      if (e.target.classList.contains("remove-doc-btn")) {
  
        const rows = container.querySelectorAll(".upload-row");
  
        if (rows.length > 1) {
          e.target.closest(".upload-row").remove();
        } else {
          alert("At least one document row must remain.");
        }
  
      }
  
    });
  
  });
  
  
  // ===============================
  // FORM VALIDATION
  // ===============================
  
  function validateForm() {
  
    const fullName = document.getElementById("fullName").value.trim();
    const dob = document.getElementById("dob").value;
    const nationality = document.getElementById("nationality").value.trim();
    const passport = document.getElementById("passport").value.trim();
    const visaType = document.getElementById("visaType").value;
  
    // Basic field validation
    if (!fullName) {
      alert("Please enter your full name.");
      return false;
    }
  
    if (!dob) {
      alert("Please enter your date of birth.");
      return false;
    }
  
    if (!nationality) {
      alert("Please enter your nationality.");
      return false;
    }
  
    if (!passport) {
      alert("Please enter your passport number.");
      return false;
    }
  
    if (!visaType) {
      alert("Please select an intended visa type.");
      return false;
    }
  
    const rows = document.querySelectorAll(".upload-row");
  
    let hasAtLeastOneFile = false;
  
    const uploadedDocumentTypes = [];
  
    // Validate document rows
    for (const row of rows) {
  
      const docType = row.querySelector("select");
      const fileInput = row.querySelector("input[type='file']");
  
      const hasType = docType && docType.value.trim() !== "";
      const hasFile = fileInput && fileInput.files.length > 0;
  
      // Document type selected but no file
      if (hasType && !hasFile) {
        alert("A selected document type is missing its file. Please upload it.");
        return false;
      }
  
      // File uploaded but no document type
      if (!hasType && hasFile) {
        alert("Please select a document type for every uploaded file.");
        return false;
      }
  
      // Track uploaded documents
      if (hasFile) {
        hasAtLeastOneFile = true;
        uploadedDocumentTypes.push(docType.value);
      }
  
    }
  
    // Require at least one document
    if (!hasAtLeastOneFile) {
      alert("Please upload at least one supporting document.");
      return false;
    }
  
    // ===============================
    // VISA-SPECIFIC DOCUMENT RULES
    // ===============================
  
    if (visaType === "STUDENT" && !uploadedDocumentTypes.includes("Letter of Acceptance")) {
      alert("Student visa applications must include a Letter of Acceptance.");
      return false;
    }
  
    if (visaType === "WORKER" && !uploadedDocumentTypes.includes("Employment Offer")) {
      alert("Worker visa applications must include an Employment Offer.");
      return false;
    }
  
    if (visaType === "VISITOR" && !uploadedDocumentTypes.includes("Passport Copy")) {
      alert("Visitor visa applications must include a Passport Copy.");
      return false;
    }
  
    if (visaType === "PERMANENT RESIDENT" && !uploadedDocumentTypes.includes("Explanation Letter")) {
      alert("Permanent Resident applications must include an Explanation Letter.");
      return false;
    }
  
    return true;
  
  }
  
  
  // ===============================
  // FORM SUBMISSION
  // ===============================
  
  document.addEventListener("DOMContentLoaded", () => {
  
    const form = document.getElementById("immigrationForm");
  
    form.addEventListener("submit", async (e) => {
  
      e.preventDefault();
  
      // Run validation before submitting
      if (!validateForm()) {
        return;
      }
  
      const formData = new FormData();
  
      // Form fields
      formData.append("fullName", document.getElementById("fullName").value);
      formData.append("dob", document.getElementById("dob").value);
      formData.append("nationality", document.getElementById("nationality").value);
      formData.append("passport", document.getElementById("passport").value);
      formData.append("visaType", document.getElementById("visaType").value);
  
      // Collect uploaded documents
      const documentRows = document.querySelectorAll(".upload-row");
  
      documentRows.forEach((row) => {
  
        const type = row.querySelector("select").value;
        const fileInput = row.querySelector("input[type='file']");
  
        if(fileInput.files.length > 0){
          formData.append("documentTypes[]", type);
          formData.append("documents[]", fileInput.files[0]);
        }
  
      });
  
      try {
  
        const response = await fetch("http://localhost:8080/v1/api/applications", {
          method: "POST",
          body: formData
        });
  
        if(response.ok){
  
          alert("Application submitted successfully to Wonderland! 🫖");
  
          //form.reset();
          const result = await response.json();
          window.location.href = `result.html?id=${result.id}`;
  
        } else {
  
          alert("Something went wrong while submitting your application.");
  
        }
  
      } catch(error){
  
        console.error(error);
  
        alert("Network error while contacting the Wonderland Border Office.");
  
      }
  
    });
  
  });