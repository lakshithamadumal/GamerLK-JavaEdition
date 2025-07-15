async function Register() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    const response = await fetch(
        "../../SignUp",
        {
            method: "POST",
            body: userJson,
            headers: {
                "Content-type": "application/json"
            }
        }
    );

    //Alert
    var notyf = new Notyf({
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) { //Success
        const json = await response.json();
        if (json.status) { //True
            window.location = "/Gamerlk/en/pages/verify-email.html";
        } else {//When Status False
            notyf.error(json.message);
        }

    } else {
        notyf.error("Registration failed. Please try again later.");
    }

}