
function generarFechas() {
	const select = document.getElementById("fechaSelect");
	const hoy = new Date();
	let contador = 0;

	while (contador < 14) {
		hoy.setDate(hoy.getDate() + 1);

		// Verificamos si es sábado (6) o domingo (0)
		if (hoy.getDay() !== 0 && hoy.getDay() !== 6) {
			const fecha = hoy.toISOString().split('T')[0]; // Solo tomamos la parte de la fecha en formato yyyy-mm-dd
			const nombreDia = hoy.toLocaleString('es-ES', { weekday: 'long' });

			// Creamos la opción y la agregamos al select
			const option = document.createElement("option");
			option.value = fecha;
			option.textContent = `${nombreDia}, ${fecha}`;
			select.appendChild(option);

			contador++;
		}
	}
}

// Función que se llama cuando se selecciona una fecha en el select
function manejarSeleccion() {
	const select = document.getElementById("fechaSelect");
	const inputDate = document.getElementById("appointmentDate");

	// Obtenemos la fecha seleccionada y la asignamos al input de tipo date
	inputDate.value = select.value;
}

// Llamamos a la función para agregar las fechas al select
generarFechas();

// Asignamos el evento para manejar la selección
document.getElementById("fechaSelect").addEventListener("change", manejarSeleccion);