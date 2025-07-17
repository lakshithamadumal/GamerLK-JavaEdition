window.onload = async function () {

    const response = await fetch("../../GetUserDetails");
    if (response.ok) {
        const json = await response.json();


        document.getElementById("ProfileFullName").innerHTML = `${json.firstName} <span class="sub-brand">${json.lastName}</span>`;
        document.getElementById("ProfileEmail").innerHTML = `${json.email}`;


        // Check status and toggle visibility
        const verifiedDiv = document.querySelector(".profile-verified");
        const suspendedDiv = document.querySelector(".profile-suspended");

        if (json.status === "Active") {
            verifiedDiv.style.display = "block";
            suspendedDiv.style.display = "none";
        } else {
            verifiedDiv.style.display = "none";
            suspendedDiv.style.display = "block";
        }

    }
    else {

    }




// Star
    const stardotContainer = document.querySelector(".stars-dot");

    // Clear any existing stars (optional if the function is called once)
    stardotContainer.innerHTML = "";

    // Detect screen size
    const isMobile = window.innerWidth <= 768;

    // Reduce stars on mobile
    const starCount = isMobile ? 40 : 100;

    for (let i = 0; i < starCount; i++) {
        const star = document.createElement("div");
        const size = Math.random() * 2 + 1 + "px";
        star.style.width = size;
        star.style.height = size;
        star.style.background = "white";
        star.style.borderRadius = "50%";
        star.style.position = "absolute";
        star.style.top = Math.random() * 100 + "%";
        star.style.left = Math.random() * 100 + "%";
        star.style.opacity = Math.random();
        stardotContainer.appendChild(star);
    }

}