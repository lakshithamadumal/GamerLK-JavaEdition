document.addEventListener("DOMContentLoaded", async function () {
    if (userLoggedIn) {
        try {
            const response = await fetch("../../GetWishlistSession");
            const result = await response.json();

            var notyf = new Notyf({
                position: {
                    x: 'center',
                    y: 'top'
                }
            });

            if (result.status) {
                notyf.success(result.message || "Wishlist synced successfully");
            } else {
                notyf.error(result.message || "Failed to sync wishlist. Try again later.");
            }
        } catch (error) {
            console.error("Error syncing wishlist:", error);
            var notyf = new Notyf({
                position: {
                    x: 'center',
                    y: 'top'
                }
            });
            notyf.error("Error syncing wishlist. Please try again.");
        }
    }
});
