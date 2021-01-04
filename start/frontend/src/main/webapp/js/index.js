
function initSSE(){
	let source = new EventSource('http://localhost:9084/bff/sse',{
		withCredentials: true 
	});
	source.addEventListener('systemLoad' , systemLoadHandler );
}

function systemLoadHandler( event ){
	let system = JSON.parse( event.data );
	if( document.getElementById( system.hostname ) ){
		document.getElementById( system.hostname ).cells[1].innerHTML = system.loadAverage.toFixed(2);
	}else{
		let tableRow = document.createElement('tr');
		tableRow.id = system.hostname ;
		tableRow.innerHTML = `<td>${system.hostname}</td><td>${system.loadAverage.toFixed(2)}</td>` ;
		document.getElementById('sysPropertiesTableBody').appendChild(tableRow);
	}
}