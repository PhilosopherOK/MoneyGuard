window.onload = async function () {
    let herokuLink = ''
    let urlGetVariantSavingJarRequest = herokuLink + '/service/getVariant/savingjar';
    let urlPostGetServiceRequest = herokuLink + '/service/getService/savingjar';
    let urlGetMineCurrencyRequest = herokuLink + '/moneyTransfer/getMineCurrency';
    let urlServicePaySavingJarRequest = herokuLink + '/service/pay/savingjar';
    let urlWithdrawSavingJarRequest = herokuLink + '/service/withdraw/savingjar/';
    let urlShowSavingJarsRequest = herokuLink + '/service/showActiveService/savingjar';

    
    const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
    
    let savingjarSelect = document.querySelector('#selectPlan');
    let selectCurr = document.querySelector('#selectCurr');
    let amountPay = document.querySelector('#amountPay');
    let amountCred = document.querySelector('#amountCred');
    let selectServiceWithdraw = document.querySelector('#selectServiceWithdraw');
    const dataContainer = document.getElementById('activeService');
    let inputId = document.querySelector('#inputId');
    const closeBtn = document.querySelector('.close')


    try {
        const response = await fetch(urlGetVariantSavingJarRequest, {
            method: 'GET',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            },
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const { data } = await response.json();
        const block1 = document.getElementById('block1');
        const block2 = document.getElementById('block2');

        function displayData(block, item) {
            const [name, percentage, months] = item;
            block.innerHTML = `
                <div><span class="label">Name:</span> ${name}</div>
                <div><span class="label">Percentage:</span> ${percentage * 100}%</div>
                <div><span class="label">Duration:</span> ${months} months</div>
            `;
        }
        displayData(block1, data[0]);
        displayData(block2, data[1]);

        data.forEach(item => {
            const name = item[0];
            const option = document.createElement('option');
            option.textContent = name;
            option.value = name;
            savingjarSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Помилка: ' + error.message);
    }

    try {
        const response = await fetch(urlGetMineCurrencyRequest, {
            method: 'GET',
            headers: {
                'X-XSRF-TOKEN': csrfToken
            },
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const { data: currencyItems } = await response.json();
        currencyItems.forEach(item => {
            let option = document.createElement('option');
            option.textContent = item;
            option.value = item;
            selectCurr.appendChild(option);
        });
    } catch (error) {
        console.error('Помилка: ' + error.message);
    }

    document.getElementById('currencyForm').addEventListener('submit', async (event) => {
        event.preventDefault();

        try {
            const response = await fetch(urlPostGetServiceRequest, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    "serviceName": savingjarSelect.value,
                    "currencyName": selectCurr.value,
                    "amount": amountCred.value
                })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const result = await response.json();

            console.log('Успішна відповідь:', result);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = result.message;
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
            closeBtn.addEventListener('click', function (e) {
                window.location.href = '/main/savingJar' 
            })

        } catch (error) {
            console.error('Помилка:', error);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = 'Network error. Try again.'
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
        }
    });

    try {
        const response = await fetch(urlShowSavingJarsRequest);

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const { success, message, data } = await response.json();
        if (success !== true) {
            const listItem = document.createElement('li');
            listItem.classList.add('list-group-item');
            listItem.classList.add('fw-lighter');
            listItem.innerHTML = `
            <h5 class="fw-lighter">${message}</h5>`;
            dataContainer.appendChild(listItem);

        } else {
            
            data.forEach(item => {
                const listItem = document.createElement('li');
                listItem.classList.add('list-group-item');
                listItem.classList.add('fw-lighter');
                listItem.innerHTML = `
                <h5 class="fw-lighter serviceId">Service Id: ${item.serviceId}</h5>
                <h5 class="fw-lighter">Service name: ${item.serviceName}</h5>
                <h5 class="fw-lighter">Currency name: ${item.currencyName}</h5>
                <h5 class="fw-lighter">Amount: ${item.amount}</h5>
                <h5 class="fw-lighter">Interest rate per month: ${item.interestRatePerMonth}</h5>
                <h5 class="fw-lighter">Service duration to: ${item.serviceDurationTo}</h5>
                <h5 class="fw-lighter">Accumulated: ${item.accumulated}</h5>
                `;
                dataContainer.appendChild(listItem);
                servId = data.serviceId
            });

            data.forEach(item => {
                let option = document.createElement('option');
                option.textContent = item.serviceId;
                option.value = item.serviceId;
                selectServiceWithdraw.appendChild(option);    
            })
        }

    } catch (error) {
        console.error('Failed to load data:', error);
    }

    document.getElementById('payForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const response = await fetch(urlServicePaySavingJarRequest, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    "id": inputId.value,
                    "amount": amountPay.value
                })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const result = await response.json();
            console.log('Успішна відповідь:', result);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = result.message;
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
            closeBtn.addEventListener('click', function (e) {
                window.location.href = '/main/savingJar' 
            })
        } catch (error) {
            console.error('Помилка:', error);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = 'Network error. Try again.'
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
        }
    });    
    
    document.getElementById('withdrawForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        try {
            const response = await fetch(`${urlWithdrawSavingJarRequest}${selectServiceWithdraw.value}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-XSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    "id": selectServiceWithdraw.value,
                })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const result = await response.json();
            console.log('Успішна відповідь:', result);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = result.message;
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
            closeBtn.addEventListener('click', function (e) {
                window.location.href = '/main/savingJar' 
            })
        } catch (error) {
            console.error('Помилка:', error);
            const modalBody = document.querySelector('#staticBackdrop .modal-body p');
            modalBody.textContent = 'Network error. Try again.'
            const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
            myModal.show();
        }
    });

    let btnWithdraw = document.querySelector('#btnWithdraw')
    if (selectServiceWithdraw.selectedIndex >= 0) {
        btnWithdraw.disabled = false;
    } else {
        btnWithdraw.disabled = true;
    }
};