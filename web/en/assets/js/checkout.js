async function Checkout() {
    const notyf = new Notyf({
        position: { x: 'center', y: 'top' }
    });

    const response = await fetch("../../LoadCartSummary");

    if (response.ok) {
        const json = await response.json();

        console.log("✅ User Email:", json.userEmail);

        if (json.cartItems && Array.isArray(json.cartItems)) {
            json.cartItems.forEach(item => {
                console.log(`🛒 Cart Item - ID: ${item.id}, Name: ${item.name}, Price: ${item.price}`);
            });
        }

        console.log("💰 Total Price:", json.totalPrice);
    } else {
        notyf.error({
            message: "Checkout failed..."
        });
    }
}
