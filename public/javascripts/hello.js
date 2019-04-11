
function renderToBlog(id) {
    window.location.href = '/blog/' + id;
}

function nextPage(next) {
    window.location.href = '/blog/summary/list?next=' + next;
}