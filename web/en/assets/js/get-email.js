window.onload = async function () {

    const response = await fetch("../../GetEmail");

    const notyf = new Notyf({
        duration: 4000, // milliseconds
        position: {
            x: 'center',
            y: 'top'
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.message === "Email Found") {//Coming Email
            document.getElementById("getemailAddress").innerHTML = `${json.email}`;
            notyf.success("Email Sent! Check your inbox.");

        } else if (json.message === "Email Not Found") {
            // email Not Coming
            window.location = "/Gamerlk/en/index.html";
        } else {
            window.location = "/Gamerlk/en/index.html";
        }
    } else {
        console.log("Log In failed. Please try again later.")
        window.location = "/Gamerlk/en/index.html";
    }

};
