function create() {
    var payload = {
        id: 0,
        date: document.getElementById('date-create').value,
        name: document.getElementById('name-create').value,
        category: document.getElementById('category-create').value,
        currency: document.getElementById('currency-create').value,
        amount: document.getElementById('amount-create').value,
        done: document.querySelector('#done-create').checked
    };
    console.log(payload);
    var http = new XMLHttpRequest();
    var url = "http://seanmcapp.herokuapp.com/wallet"; // TODO: use secret key from session
    http.open('POST', url, true);
    http.setRequestHeader('Content-type', 'application/json; charset=UTF-8');
    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            alert(http.responseText);
        }
    };
    http.send(JSON.stringify(payload));
}