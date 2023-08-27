const uploadForm = document.getElementById("uploadForm");
const fileInput = document.getElementById("fileInput");
const imageTableBody = document.getElementById("imageTableBody");

const bearerToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImV4cCI6MTY5NTcxMzIyNiwiaWF0IjoxNjkzMTIxMjI2LCJ1c2VySWQiOjF9.K2Rz5rVA-E6e-VLnvPIved5sKhvcVvbXGIwUPc2OPp8";

uploadForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const formData = new FormData();

    // Append all selected files to the FormData object
    for (const file of fileInput.files) {
        formData.append("files", file);
    }

    fetch("/api/v1/images/upload", {
        method: "POST",
        body: formData,
        headers: {
            'Authorization': `Bearer ${bearerToken}`, // Add Bearer token to headers
        },
    })
        .then((response) => response.text())
        .then((message) => {
            alert(message);
            fetchUploadedImages();
        })
        .catch((error) => console.error(error));
});

function fetchUploadedImages() {
    fetch("/api/v1/images")
        .then((response) => response.json())
        .then((data) => {
            // Check if data is an array
            if (Array.isArray(data)) {
                imageTableBody.innerHTML = "";
                data.forEach((image) => {
                    const row = imageTableBody.insertRow();
                    row.innerHTML = `
                        <td>${image.id}</td>
                        <td>${image.name}</td>
                        <td>${(image.size / 1024).toFixed(2)} KB</td>
                        <img src="/api/v1/images/preview/${image.name}" alt="${image.name}" width="300"/>
                        <td>${image.vehicleId}</td>
                        <td><a href="/api/v1/images/download/${image.name}" target="_blank">Download</a></td>
                    `;
                });
            } else {
                console.error("Data is not an array:", data);
            }
        })
        .catch((error) => console.error(error));
}

fetchUploadedImages();