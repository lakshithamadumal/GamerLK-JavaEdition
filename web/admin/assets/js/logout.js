async function LogOut() {
    const response = await fetch("../AdminSignOut");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location.reload();
            window.location = "/Gamerlk/admin/auth-signin.html";
        } else {
            window.location.reload();
        }
    } else {
        console.log(response);
    }
}