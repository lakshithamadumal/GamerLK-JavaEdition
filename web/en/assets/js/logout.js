async function LogOut() {
    const response = await fetch("../SignOut");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "/Gamerlk/en/pages/login.html"; 
        } else {
            window.location.reload();
        }
    } else {
        console.log(response);
    }
}