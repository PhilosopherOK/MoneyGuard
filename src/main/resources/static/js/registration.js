window.onload = function () {
    let checkName = (name)=>/[a-zA-Z]/g.test(name);
    let checkNumber = (number)=>/^\+?[0-9]{10,12}$/.test(number);
    let checkEmail = (email)=>/^([a-z\d\._-]+)@([a-z\d_-]+)\.([a-z]{2,8})(\.[a-z]{2,8})?$/i.test(email);
    let checkPassword = (password)=>/^(?=.*[a-z])(?=.*\d)(?=.*[A-Z])(?=.*[!@#$%^&*]).{10,30}$/.test(password);
    
    let btn = document.querySelector('#btn')
    let inputFirstName = document.querySelector('#inputFirstName')
    let inputSecondName = document.querySelector('#inputSecondName')
    let inputDateOfBirth = document.querySelector('#inputDateOfBirth')
    let inputPhone = document.querySelector('#inputPhoneNumber')
    let inputEmail = document.querySelector('#inputEmail')
    let inputPassword1 = document.querySelector('#inputPassword1')
    let inputPassword2 = document.querySelector('#inputPassword2')

    const inputElements = document.querySelectorAll('input');

    inputElements.forEach(input => {
        input.addEventListener('input', toggleButtonState);
    });

    function checkFullInputs() {
        let isValid = true;
        inputElements.forEach(input => {
            if (input.value === '') {
                isValid = false;
            }
        });
        return isValid;
    }

    function toggleButtonState() {
        if (checkFullInputs()) {
            btn.disabled = false;
            btn.style.backgroundColor = '#7a5e3c';
            btn.style.border = 'solid 2px #7a5e3c'
            btn.addEventListener('mouseenter', ()=>
                btn.style.backgroundColor = '#57432b')
            btn.addEventListener('mouseleave', ()=>
                btn.style.backgroundColor = '#7a5e3c')
            btn.style.cursor = 'pointer';
        } else {
            btn.disabled = true;
            btn.style.backgroundColor = '#ada8a2';
            btn.style.cursor = 'default';
        }
    }    

    btn.addEventListener('click', function() {
        let errorMessage = '';   

        if (!checkName(inputFirstName.value)) {
            errorMessage += 'Invalid first name\n';
        }
        
        if (!checkName(inputSecondName.value)) {
            errorMessage += 'Invalid second name\n';
        }

        if (!checkEmail(inputEmail.value)) {
            errorMessage += 'Invalid email\n';
        }
        
        if (!checkNumber(inputPhone.value)) {
            errorMessage += 'Invalid phone number\n';
        }

        if (!checkPassword(inputPassword1.value)) {
            errorMessage += 'Invalid password\n';
        }

        if (inputPassword1.value !== inputPassword2.value) {
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

        const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
        const formData = {
            firstName: inputFirstName.value, 
            secondName: inputSecondName.value, 
            dateOfBirth: inputDateOfBirth.value, 
            phoneNumber: inputPhone.value, 
            email: inputEmail.value, 
            password: inputPassword1.value
        };

        fetch('/api/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Check your data');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    console.log('Success:', data.message);
                    alert(data.message);
                    window.location.href = '/login';
                } else {
                    throw new Error(data.message);
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Error in registration: ' + error.message);
            });
    }
}
