function getPokemonWithId(id) {
    location.href = "/api/pokemons/get/pokemon?id=" + id;
}

function getPokemonWithName(name) {
    location.href = "/api/pokemons/get/pokemon?name=" + name;
}

function setElementId(id, value) {
    const element = document.getElementById(id)
    element.value = value;
}

function clearElementId(id) {
    const element = document.getElementById(id)
    element.value = "";
}

function goBackToPreviousTab() {
    const previousTab = document.referrer;
    if (previousTab.includes("random")) {
        parent.history.go(-2);
    } else {
        parent.history.back();
    }
}

function reloadPage() {
    window.location.reload();
}