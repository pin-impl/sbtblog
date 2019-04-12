weui.searchBar('#search_bar');

function renderToBlog(id) {
    window.location.href = '/blog/' + id;
}

function nextPage(next) {
    window.location.href = '/blogs?next=' + next;
}