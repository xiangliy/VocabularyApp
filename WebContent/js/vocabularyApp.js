/**
 * 
 */

app.controller('SearchController', function ($scope, ngTableParams) {
	
	$scope.search = function() {
		var data = [
                    {name: "foaf:name"},
                    {name: "foaf:firstname"},
                    {name: "foaf:secondname"},
                    {name: "foaf:thirdname"},
                    {name: "foaf:test1"},
                    {name: "foaf:lastname"},
                    {name: "foaf:email"},
                    {name: "dc:dc1"},
                    {name: "sko:sko1"},
                    {name: "dc:dc3"},
                    {name: "dc:dcdsf"},
                    {name: "rdfs:class"},
                    {name: "rdfs:domain"},
                    {name: "rdfs:range"},
                    {name: "rdf:type"},
                    {name: "owl:Class"},
                    {name: "text:query"},
                    {name: "text:query1"},
                    {name: "text:fsd"},
                    {name: "text:fsdf"}
                ];
    
	    $scope.searchTableParams = new ngTableParams({
	        page: 1,            
	        count: 10           
	    }, {
	        total: data.length, 
	        getData: function($defer, params) {
	            $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	        }
	    });
    };
    
    $scope.addResult = function(userName) {
    	$scope.resultValue = userName;
    };
    
    var data;
    
    $scope.switchGoManageDialog = function() {
    	$scope.selection = "manageDialog";
    	
    	data = [
                    {name: "foaf"},
                    {name: "dc"},
                    {name: "sdc"},
                    {name: "sfd"},
                    {name: "wef"},
                    {name: "fd"},
                    {name: "ergfd"},
                    {name: "hrt"},
                    {name: "iol"},
                    {name: "op;"},
                    {name: "xcvz"},
                    {name: ",k"},
                    {name: "mgh"},
                    {name: "ht"},
                    {name: "vcb"},
                    {name: "dfg"},
                    {name: "grgdf"},
                    {name: "bfbv"},
                    {name: "jtyj"},
                    {name: "sdfow"}
                ];
    
	    $scope.manageTableParams = new ngTableParams({
	        page: 1,            
	        count: 10           
	    }, {
	        total: data.length, 
	        getData: function($defer, params) {
	            $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
	        }
	    });
    }
    
    $scope.switchToManageDialog = function() {
    	$scope.selection = "searchDialog";
    }
    
    $scope.switchToAddDialog = function() {
    	$scope.selection = "addVocabDialog";
    }
    
    $scope.deleteItem = function(userName) {
    	var index = data.indexOf(userName);
    	if (index > -1) {
    		data.splice(index, 1);
    	}
    }
    
    $scope.addVocabToTable = function() {
    	alert("add");
    }
});