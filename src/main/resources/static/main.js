const CLIENT_ID = "861068135718-dnnfh5qp7jg5fegc78bd1e96ridu8qrp.apps.googleusercontent.com";

myStorage = window.localStorage;

function start() {

    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: CLIENT_ID,
            <!-- nodehill.com blog auto-converts non https-strings to https, thus the concatenation. -->
            scope: "https://www.googleapis.com/auth/calendar.events"
        });
    });
}

$('#signinButton').click(function() {
// signInCallback defined in step 6.
    auth2.grantOfflineAccess().then(signInCallback);
});

$('#loginForm').submit(function (e) {
    e.preventDefault();
    $.ajax({
        type: "POST",
        url: 'http://localhost:8080/user/login',
        data: JSON.stringify({
            username: $("#username").val(),
            password: $("#password").val()
        }),
        contentType: 'application/json',
        processData: false,
        success: function (data, textStatus, request) {
            console.log(request.getResponseHeader('Authorization'));

            let authorization = request.getResponseHeader("Authorization");

            localStorage.setItem('Authorization', authorization);
            $('#loginForm').attr('style', 'display: none');

        },
        error: function (request, textStatus, errorThrown) {
            alert(request.getResponseHeader('some_header'));
        }
    });

});


$('#registerForm').submit(function (e) {
    e.preventDefault();
    $.ajax({
        type: "POST",
        url: 'http://localhost:8080/user/register',
        data: JSON.stringify({
            username: $("#usernameReg").val(),
            password: $("#passwordReg").val(),
            confirmPassword: $("#confirmPassword").val()

        }),
        contentType: 'application/json',
        processData: false,
        success: function (data, textStatus, request) {
        },
        error: function (request, textStatus, errorThrown) {
            alert(request.getResponseHeader('some_header'));
        }
    });

});

function signInCallback(authResult) {
    console.log('authResult', authResult);
    if (authResult['code']) {

        // Hide the sign-in button now that the user is authorized, for example:
        $('#signinButton').attr('style', 'display: none');

        // Send the code to the server
        $.ajax({
            type: 'POST',
            <!-- nodehill.com blog auto-converts non https-strings to https, thus the concatenation. -->
            url: 'http://localhost:8080/storeauthcode',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Authorization': localStorage.getItem("Authorization")
            },
            contentType: 'application/octet-stream; charset=utf-8',
            success: function(result) {
                // Handle or verify the server response.
            },
            processData: false,
            data: authResult['code']
        });
    } else {
        // There was an error.
    }
}