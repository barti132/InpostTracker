const form = document.getElementById('tracking-form');
const input = document.getElementById('tracking-number');
const button = document.getElementById('submit-button');
const result = document.getElementById('result');
const historyList = document.getElementById('history');

function showMessage(text, type = '') {
    result.textContent = text;
    result.className = type;
}

function clearResult() {
    showMessage('');
    historyList.replaceChildren();
}

function formatDate(isoString) {
    const date = new Date(isoString);
    return Number.isNaN(date.getTime()) ? String(isoString) : date.toLocaleString('pl-PL');
}

function renderHistory(details) {
    historyList.replaceChildren();

    if (!Array.isArray(details) || details.length === 0) {
        const item = document.createElement('li');
        item.className = 'empty';
        item.textContent = 'Brak historii zdarzeń.';
        historyList.appendChild(item);
        return;
    }

    for (const detail of details) {
        const item = document.createElement('li');

        const date = document.createElement('time');
        date.dateTime = detail.datetime;
        date.textContent = formatDate(detail.datetime);

        const status = document.createElement('span');
        status.className = 'event-status';
        status.textContent = detail.status;

        item.append(date, status);
        historyList.appendChild(item);
    }
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const trackingNumber = input.value.trim();

    clearResult();

    if (trackingNumber === '') {
        showMessage('Wpisz numer przesyłki.', 'error');
        return;
    }

    button.disabled = true;
    showMessage('Sprawdzam...', 'loading');

    try {
        const response = await fetch('/api/parcels/' + encodeURIComponent(trackingNumber));

        const data = await response.json().catch(() => null);

        if (response.ok && data) {
            showMessage('Status: ' + data.status, 'success');
            renderHistory(data.tracking_details);
        } else if (data && data.message) {
            showMessage('Błąd (' + data.code + '): ' + data.message, 'error');
        } else {
            showMessage('Nieoczekiwana odpowiedź serwera (HTTP ' + response.status + ').', 'error');
        }
    } catch (error) {
        showMessage('Nie udało się połączyć z serwerem.', 'error');
    } finally {
        button.disabled = false;
    }
});