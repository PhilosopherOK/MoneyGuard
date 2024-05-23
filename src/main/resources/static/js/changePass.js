

document.addEventListener('DOMContentLoaded', function() {

    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
    let checkPassword = (password)=>/^(?=.*[a-z])(?=.*\d)(?=.*[A-Z])(?=.*[!@#$%^&*]).{10,30}$/.test(password);
    let btn = document.querySelector('#btn')

    if (!token) {
        window.location.href = '/login'; 
    }

    var newPassword = document.querySelector('#newPassword');
    var confirmPassword = document.querySelector('#confirmPassword');

    btn.addEventListener('click', function() {
        let errorMessage = '';   
    
        if (!checkPassword(newPassword.value)) {
            errorMessage += 'Invalid password. Please, use the instruction. \n';
        }

        if (newPassword.value !== confirmPassword.value) {
            errorMessage += 'Different passwords\n';
        }

        if (errorMessage !== '') {
            alert('Validation error:\n' + errorMessage);
        } else {
            console.log('Validation passed.');
            submitForm()
        }

    });

    function submitForm() {

        fetch('/api/registration/changePass?token=' + token, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                "newPassword": newPassword.value,
                "confirmPassword": confirmPassword.value
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Check your data');
            }
            return response.json();
        })
        .then(response => {
            if (response.success) {
                console.log('Success:', response.message);
                alert(response.message);
                window.location.href = '/login';
            } else {
                throw new Error(response.message);
            }
        })
        .catch(error => {
            alert('Помилка: ' + error.message);
        });
    }
});