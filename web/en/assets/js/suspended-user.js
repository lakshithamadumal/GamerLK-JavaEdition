document.addEventListener("DOMContentLoaded", async function () {
    // Get current page as "index.html" or "pages/xxx.html"
    let currentPage = window.location.pathname.replace(/^.*\/en\//, "");

    // Set fetch path based on current page
    let fetchPath = currentPage === "index.html" ? "../GetUserDetails" : "../../GetUserDetails";

    try {
        const response = await fetch(fetchPath);
        if (response.ok) {
            const user = await response.json();

            const allowedPages = [
                "index.html",
                "pages/account-settings.html",
                "pages/game-store.html",
                "pages/my-downloads.html",
                "pages/order-history.html",
                "pages/support-center.html",
                "pages/user-profile.html",
                "pages/wishlist.html",
                "pages/game-details.html"
            ];

            const suspendedAlertShown = sessionStorage.getItem("suspendedAlertShown");
            const welcomeAlertShown = sessionStorage.getItem("welcomeAlertShown");

            if (user.status === "Inactive") {
                if (allowedPages.includes(currentPage)) {
                    if (!suspendedAlertShown) {
                        Swal.fire({
                            icon: "warning",
                            title: "Account Suspended",
                            text: "You only have limited access",
                            confirmButtonText: "OK",
                            background: "#141415",
                            color: "#ffffff",
                            iconColor: "#ff6b27",
                            confirmButtonColor: "#df040a",
                            customClass: { popup: 'shadow-custom' }
                        });
                        sessionStorage.setItem("suspendedAlertShown", "true");
                    }
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Access Denied",
                        text: "Suspended users cannot access this page.",
                        confirmButtonText: "OK",
                        background: "#141415",
                        color: "#ffffff",
                        iconColor: "#df040a",
                        confirmButtonColor: "#df040a",
                        customClass: { popup: 'shadow-custom' }
                    }).then(() => {
                        window.location.href = "../index.html";
                    });
                }

                document.querySelectorAll("a, [onclick]").forEach(function (link) {
                    let href = link.getAttribute("href");
                    if (!href) {
                        const onclick = link.getAttribute("onclick");
                        if (onclick && onclick.includes("location.href")) {
                            href = onclick.match(/location\.href=['"]([^'"]+)['"]/);
                            href = href ? href[1] : null;
                        }
                    }
                    if (href && !allowedPages.includes(href)) {
                        link.addEventListener("click", function (e) {
                            e.preventDefault();
                            Swal.fire({
                                icon: "error",
                                title: "Access Denied",
                                text: "Suspended users cannot access this page.",
                                confirmButtonText: "OK",
                                background: "#141415",
                                color: "#ffffff",
                                iconColor: "#df040a",
                                confirmButtonColor: "#df040a",
                                customClass: { popup: 'shadow-custom' }
                            });
                        });
                    }
                });
            } else if (user.status === "Active") {
                if (!welcomeAlertShown) {
                    const fullName = `${user.firstName} ${user.lastName}`;
                    Swal.fire({
                        icon: "success",
                        title: `Welcome, ${fullName}!`,
                        text: "Enjoy your experience.",
                        confirmButtonText: "Got it",
                        background: "#141415",
                        color: "#ffffff",
                        iconColor: "#28a745",
                        confirmButtonColor: "#28a745",
                        customClass: {
                            popup: 'shadow-custom swal-rounded'
                        }
                    });
                    sessionStorage.setItem("welcomeAlertShown", "true");
                }
            }
        }
    } catch (e) {
        // Handle error if needed
    }
});






function getCurrentPage() {
    const path = window.location.pathname; // e.g. "/project/index.html"
    const page = path.split("/").pop();    // get last part => "index.html"
    return page;
}

// 🔍 Usage
const currentPage = getCurrentPage();
console.log("You're on:", currentPage);
