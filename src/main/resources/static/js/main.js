function DownloadClient() {
	$.ajax({
		type : "GET",
		contentType : "application/json",
		url : "/downloadClient",
		cache : false,
		timeout : 60000,
		error : function(e) {
			console.log("ERROR : ", e);
		}
	});
}