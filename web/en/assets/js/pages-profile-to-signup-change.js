window.onload = async function () {
    const profile = document.querySelector(".ProfileTo");
    const signup = document.querySelector(".SignUpTo");
    const cart = document.querySelector(".CartTo");
    const download = document.querySelector(".DownloadTo");
    const community = document.querySelector(".CommunityTo");
    const logout = document.querySelector(".LogoutTo");
    const settings = document.querySelector(".SettingsTo");

    try {
        const response = await fetch("../../GetUserDetails");

        if (!response.ok) {
            throw new Error("User not logged in");
        }

        const json = await response.json();

        document.getElementById("NavFullName").innerHTML = `${json.firstName} ${json.lastName}`;
        signup.style.display = "none";
    } catch (error) {
        profile.style.display = "none";
        cart.style.display = "none";
        download.style.display = "none";
        community.style.display = "none";
        logout.style.display = "none";
        settings.style.display = "none";

    }

    staronindex();
};

function staronindex() {

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
};
