const REFRESH_INTERVAL = 600000

function refreshDashboard () {
  fetch('/dashboard')
    .then(response => response.text())
    .then(responseBody => dashboard.innerHTML = responseBody)
    .then(_ => setLastUpdateTime())

  setTimeout(refreshDashboard, REFRESH_INTERVAL)
}

function setLastUpdateTime () {
  const lastUpdateTimeElement = document.getElementById("#lastupdatetime")
  lastUpdateTimeElement.textContent = new Date().toLocaleTimeString()
}

refreshDashboard()
