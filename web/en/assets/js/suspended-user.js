document.addEventListener("DOMContentLoaded", async function () {
    try {
        const response = await fetch("../GetUserDetails");
        if (response.ok) {
            const user = await response.json();

            // If user is inactive, show suspended message
            if (user.status === "Inactive") {
                Swal.fire({
                    icon: "warning",
                    title: "Account Suspended",
                    text: "You only have limited access",
                    confirmButtonText: "OK",
                    background: "#141415",           // --bg-main
                    color: "#ffffff",                // text color
                    iconColor: "#ff6b27",            // --accent-orange
                    confirmButtonColor: "#df040a",   // --accent-red
                    customClass: {
                        popup: 'shadow-custom'
                    }
                });

                // Allow only specific pages
                document.querySelectorAll(
                    'aside.sidebar a[href="pages/game-store.html"], ' +
                    'aside.sidebar a[href="pages/game-details.html"], ' +
                    'aside.sidebar a[href="pages/wishlist.html"], ' +
                    'aside.sidebar a[href="pages/my-downloads.html"]'
                ).forEach(function (link) {
                    link.addEventListener("click", function (e) {
                        e.preventDefault();
                        Swal.fire({
                            icon: "error",
                            title: "Access Denied",
                            text: "Suspended users cannot access this page.",
                            confirmButtonText: "OK",
                            background: "#141415",           // --bg-main
                            color: "#ffffff",                // white text for contrast
                            iconColor: "#df040a",            // --accent-red
                            confirmButtonColor: "#df040a",   // same red for consistency
                            customClass: {
                                popup: 'shadow-custom'
                            }
                        });

                    });
                });
            } else if (user.status === "Inactive") {
                const fullName = `${json.firstName} ${json.lastName}`;

                Swal.fire({
                    icon: "success",
                    title: `Welcome, ${fullName}!`,
                    text: "Enjoy your experience.",
                    confirmButtonText: "Got it",
                    background: "#141415",           // --bg-main
                    color: "#ffffff",                // text color
                    iconColor: "#ff6b27",            // --accent-orange
                    confirmButtonColor: "#df040a",   // --accent-red
                    customClass: {
                        popup: 'shadow-custom'
                    }
                });

            }
        }
    } catch (e) {
        // Handle error if needed
    }
});