

Array.from(document.querySelectorAll('pre code')).forEach(function(node) {
    var lines = node.textContent.split('\n').length - 1
    var $numbering = document.createElement('ul')
    $numbering.className = 'pre-numbering'
    node.className = 'has-numbering'
    node.parentNode.appendChild($numbering)
    for (var i = 1; i < lines; i++) {
        var num = document.createElement('li')
        num.innerText = i + ''
        $numbering.append(num)
    }
})