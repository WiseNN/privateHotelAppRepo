class Billing
{

	constructor()
	{
		//....
		

		// this.createLineItemDescriptionTextCell = createLineItemDescriptionTextCell().bind(this);
		// this.createHoursTextCell = createHoursTextCell().bind(this);
		// this.createRateTextCell = createRateTextCell().bind(this);
		// this.createAmountTextCell = createAmountTextCell().bind(this);
		
		
	}



	addRowToTable(lineDescrText, hrsText, rateText, amountText)
	{

		// Find a <table> element with id="myTable":
		debugger;
		var table = document.getElementById("tableView");

		// Create an empty <tr> element and add it to the 1st position of the table:
		var trElm = table.insertRow(table.rows.length-2);

		//create table cells from given param string values
		var descrCell = this.createLineItemDescriptionTextCell(lineDescrText);
		var hrsCell = this.createHoursTextCell(hrsText);
		var rateCell = this.createRateTextCell(rateText);
		var amountCell = this.createAmountTextCell(amountText);

		//add table cells to table row
		trElm.appendChild(descrCell);
		trElm.appendChild(hrsCell);
		trElm.appendChild(rateCell);
		trElm.appendChild(amountCell);


		

		// table[0].insertBefore(trElm, table[0].childNodes[9]);
	}


	createLineItemDescriptionTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("descrTableDataStyle");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "lineItemDescriptionSpanText";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}

	createHoursTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("smallTableDateStyle");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "hoursSpanText";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}

	createRateTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("smallTableDateStyle");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "rateSpanText";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}


	createAmountTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("smallTableDateStyle");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "amountSpanText";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}


	setRoomDescriptionTextBlock(withText)
	{
		debugger;
		var descrBlock = document.getElementById("roomDescriptionTextBlock")
		descrBlock.innerHTML = withText
	}

	setNotesTextBlock(withText)
	{
		debugger;
		var notesBlock = document.getElementById("notesSpanText")
		notesBlock.innerHTML = withText
	}
	setReservationNumberText(withText)
	{
		debugger;
		var reservationNumber = document.getElementById("reservationSpanText")
		reservationNumber.innerHTML += " "+withText
	}
	
	setDateText(withText)
	{
		debugger;
		//Date format >> March 20, 2018 <<
		var purchaseDate = document.getElementById("dateSpanText")
		purchaseDate.innerHTML = withText
	}

	setPurchaseTotalText(withText)
	{
		debugger;
		//Date format >> March 20, 2018 <<
		var purchaseTotalText = document.getElementById("totalSpanText")
		purchaseTotalText.innerHTML = withText
	}
	



}



 document.addEventListener("DOMContentLoaded", function(event) {

		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
        //
		// bill.setRoomDescriptionTextBlock();
		// bill.setNotesTextBlock();
		// bill.setReservationNumberText();
		// bill.setPurchaseTotalText("withText");
		// bill.setDateText("withText")
  });

	// window.onload = function(){
	// 	var bill = new Billing();
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText").bind(this);
	// }();


var bill = new Billing();




