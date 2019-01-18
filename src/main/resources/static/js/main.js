function StopServer() {
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url : "/stopServer/",
		cache : false,
		timeout : 60000,
		success : function(data) {
			console.log(data);
		},
		error : function(e) {
			console.log("ERROR : ", e);
		}
	});
}

function StartServer() {
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url : "/startServer/",
		cache : false,
		timeout : 60000,
		success : function(data) {
			console.log(data);
		},
		error : function(e) {
			console.log("ERROR : ", e);
		}
	});
}