<html>
	<head></head>
	<body>
	<button><a href="http://localhost/metawear-ads/index.php">Data Collecting Page</a></button>
		<?php
		$servername = "localhost";
		$username = "root";
		$password = "";
		$dbname = "metaweardb";
		
		// Create connection
		$conn = new mysqli($servername, $username, $password, $dbname);
		// Check connection
		if ($conn->connect_error) {
		    die("Connection failed: " . $conn->connect_error);
		} 
		
		$sql = "SELECT * FROM tbl_data ORDER BY id DESC";
		$result = $conn->query($sql);
	?>
	<table border="1" cellspacing="2" cellpadding="5">
		<tr>
			<th>ID</th>
			<th>TIME</th>
			<th>TEMPERATURE</th>
			
		</tr>
		
		<?php	
		if ($result->num_rows > 0) {
		    // output data of each row
		    while($row = $result->fetch_assoc()) {
		?>
		    <tr>
		    	<td><?php echo $row["id"]; ?></td>
		    	<td><?php echo $row["time"]; ?></td>
		    	<td><?php echo $row["temperature"]; ?></td>
		    	
		    </tr>
		<?php    
		    }
		} else {
		    echo "0 results";
		}
		$conn->close();
		?>
	</body>
</html>

		



				
				 