function validateForm() {
    function setSuccess(id) {
        document.getElementById(id).parentElement.classList.remove("has-danger")
    }

    function setError(id) {
        document.getElementById(id).parentElement.classList.add("has-danger")
    }

    let form = document.forms["form"];

    let name = form["name"].value
    if (!name || name.length === 0 ) {
        setError("name-form")
        return false;
    } else setSuccess("name-form")

    let currency = form["currency"].value
    if (!currency || currency.length === 0 ) {
        setError("currency-form")
        return false;
    } else setSuccess("currency-form")

    let amount = form["amount"].value
    if (!amount || !/^-?[\d.]+(?:e-?\d+)?$/.test(amount)) {
        setError("amount-form")
        return false;
    } else setSuccess("amount-form")

    let category = form["category"].value
    if (!category || category.length === 0 ) {
        setError("category-form")
        return false;
    } else setSuccess("category-form")

    // "done" no need to validate

    let account = form["account"].value
    if (!account || account.length === 0 ) {
        setError("account-form")
        return false;
    } else setSuccess("account-form")

    return true;
}
