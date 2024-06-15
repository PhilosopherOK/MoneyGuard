window.onload =function(){
    let herokuLink = ''

    let urlGetMineCurrencyRequest = herokuLink + '/moneyTransfer/getMineCurrency'
    let urlPostExchangeRequest = herokuLink + '/moneyTransfer/currencyExchange'

    const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

    let currencyNameFrom = document.querySelector('#currencyNameFrom');
    let currencyNameTo = document.querySelector('#currencyNameTo');
    let transferAmount = document.querySelector('#transferAmount');
    

    fetch(urlGetMineCurrencyRequest, {
        method: 'GET',
        headers: {
        'X-XSRF-TOKEN': csrfToken
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json()
    })
    .then(response => {
        let currencyItems = response.data;
        if (currencyItems.length > 0) {
            currencyItems.forEach(item => {
                console.log(currencyItems);
                console.log(item);
                let option = document.createElement('option');
                let otherOption = document.createElement('option')
                option.textContent = item;
                otherOption.textContent = item;
                option.value = item;
                otherOption.value = item;
                currencyNameFrom.appendChild(option);
                currencyNameTo.appendChild(otherOption);
                
            })
        }
    })
    .catch(error => {
        console.error('Помилка:', error);
    });
    

    document.getElementById('currencyForm').addEventListener('submit', function(e) {
        e.preventDefault()
          

        fetch(urlPostExchangeRequest, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken
            },
            body: JSON.stringify({
                "currencyNameFrom": currencyNameFrom.value,
                "currencyNameTo": currencyNameTo.value,
                "transferAmount": transferAmount.value
            })
        })
            .then(response => {
                if (!response.ok) {
                throw new Error('Network response was not ok');
                }
                return response.json();
            })
    
            .then(response => {
                console.log('Успішна відповідь:', response);
                const modalBody = document.querySelector('#staticBackdrop .modal-body p');
                modalBody.textContent = response.message;
                const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
                myModal.show();
            })
            .catch(error => {
                console.error('Помилка:', error);
                const modalBody = document.querySelector('#staticBackdrop .modal-body p');
                modalBody.textContent = 'Network error. Try again.'
                const myModal = new bootstrap.Modal(document.getElementById('staticBackdrop'));
                myModal.show();
            });
    });    


}