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
		var trElm = table.insertRow(table.rows.length-1);

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
		tdElm.classList.add("descriptionTD");
        tdElm.classList.add("hardBorders");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		// spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "itemDescrLineItem";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}

	createHoursTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("lengthTD");
        tdElm.classList.add("hardBorders");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "lengthLineItem";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}

	createRateTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("rateTD");
        tdElm.classList.add("hardBorders");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		// spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "rateLineItem";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}


	createAmountTextCell(withText)
	{

		var tdElm = document.createElement("td");
		tdElm.classList.add("amountTD");
        tdElm.classList.add("hardBorders");

		var pElm = document.createElement("p");
		tdElm.classList.add("MsoNormal");

		var spanElm = document.createElement("span");
		// spanElm.classList.add("spanStyleHighLineHeight");
		spanElm.id = "amountLineItem";

		var textNode = document.createTextNode(withText);

		tdElm.appendChild(pElm);

		pElm.appendChild(spanElm);

		spanElm.appendChild(textNode);


		return tdElm;

	}

	createFinalOutBillingAddress(fullName, adr1,adr2,cityStateZipcode)
	{

		var tdElm = document.getElementById("finalBillAdrTD");
		
		var idNames = ["billingName","billingAddress1","billingAddress2","cityStateZipcode"];
		for(var i=0;i<idNames.length;i++)
		{
			var pElm = document.createElement("p");
			pElm.classList.add("MsoNormal");
			pElm.classList.add("billingTextAlign");
			

			var spanElm = document.createElement("span");
			spanElm.id = idNames[i];

			var textNode = document.createTextNode(arguments[i]);

			tdElm.appendChild(pElm);

			pElm.appendChild(spanElm);

			spanElm.appendChild(textNode);
		}

		return tdElm;
		
	}

	createFinalOutBillingCardInfo(paymentName, cardNumber,exprDate)
	{

		var tdElm = document.getElementById("paymentCardExprDate");
		
		
		var idNames = ["paymentName","paymentCardNumber","paymentCardExprDate"];
		for(var i=0;i<idNames.length;i++)
		{
			var pElm = document.createElement("p");
			pElm.classList.add("MsoNormal");
			pElm.classList.add("billingTextAlign");
			

			var spanElm = document.createElement("span");
			spanElm.id = idNames[i];

			var textNode = document.createTextNode(arguments[i]);

			tdElm.appendChild(pElm);

			pElm.appendChild(spanElm);

			spanElm.appendChild(textNode);
		}

		return tdElm;
		
	}
	setDocumentTitle(withText)
	{
		var titleSpan = document.getElementById("docTitle")
		titleSpan.innerHTML = withText;
		document.title = "Hotel Mangz’elia "+withText;
	}


	setRoomDescriptionTextBlock(withText)
	{
		debugger;
		var descrBlock = document.getElementById("roomDescription");
		descrBlock.innerHTML = withText;
	}

	setNotesTextBlock(withText)
	{
		debugger;
		var notesBlock = document.getElementById("notesSpecialRequests");
		notesBlock.innerHTML = withText;
	}
	setReservationNumberText(withText)
	{
		debugger;
		var reservationNumber = document.getElementById("rezBillingNum");
		reservationNumber.innerHTML += " "+withText;
	}
	
	setDateText(withText)
	{
		debugger;
		//Date format >> March 20, 2018 <<
		var purchaseDate = document.getElementById("rezDate");
		purchaseDate.innerHTML = withText;
	}

	setPurchaseTotalText(withText)
	{
		debugger;
		//Date format >> March 20, 2018 <<
		var purchaseTotalText = document.getElementById("totalLineItem");
		purchaseTotalText.innerHTML = withText;
	}

	showfinalOutBillingTable(bool)
	{
		if(bool)
		{
			var finalOutTable1 = document.getElementById("finalOutBillingP1");
			var finalOutTable2 = document.getElementById("finalOutBillingP2");
			
			finalOutTable1.style.display = "none";
			finalOutTable2.style.display = "none";



		}
	}
	
	



}



 document.addEventListener("DOMContentLoaded", function(event) {
 	debugger;
 	// var bill = new Billing();
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
		//
      //
		// bill.setRoomDescriptionTextBlock("room descr text block");
		// bill.setNotesTextBlock("notes text block");
		// bill.setReservationNumberText("123456789");
		// bill.setPurchaseTotalText("$999.99");
		// bill.setDateText("March 24, 2018");
		bill.createFinalOutBillingAddress("fullName", "adr1","adr2","cityStateZipcode");
		bill.createFinalOutBillingCardInfo("paymentName", "cardNumber","exprDate");
		// bill.showfinalOutBillingTable(false);
		// bill.setDocumentTitle("Reservation Summary");


  });

	window.onload = function(){
		// var bill = new Billing();
		// bill.addRowToTable("lineDescrText", "hrsText", "rateText", "amountText");
	}();


var bill = new Billing();




