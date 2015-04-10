/**
 * 
 */
app.controller('SearchController', function ($scope, $http, $resource, ngTableParams) {
	
	$scope.search = function(keyword) {
        var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/search/'+ keyword);
        Vocabulary.get({}, function(response){
        	if(response.HTTP_CODE == 200){
        		data = response.result;
        		
        		for (var i=VocabularCollection.length-1; i>=0; i--) {
        		    $scope.searchTableParams = new ngTableParams({
        		        page: 1,            
        		        count: 10           
        		    }, {
        		        total: data.length, 
        		        getData: function($defer, params) {
        		            $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        		        }
        		    });
        		}
        		
        		$scope.manageTableParams.reload();
        	}
		}); 
    };
    
    $scope.addResult = function(userName) {
    	$scope.resultValue = userName;
    };
    
    var VocabularCollection;
    
    $scope.switchToManageDialog = function() {
    	$scope.selection = "manageDialog";
    	
        var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/getAllVocabulary');
        Vocabulary.get({}, function(response){
			if(response.HTTP_CODE == 200){
				VocabularCollection = response.result;

			    $scope.manageTableParams = new ngTableParams({
			        page: 1,            
			        count: 10           
			    }, {
			        total: VocabularCollection.length, 
			        getData: function($defer, params) {
			            $defer.resolve(VocabularCollection.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			        }
			    });
				
			}
			else{
				alert("error");
			}
		});    
    }
    
    $scope.switchToSearchDialog = function() {
    	$scope.selection = "searchDialog";
    }
    
    $scope.switchToAddDialog = function() {
    	$scope.selection = "addVocabDialog";
    }
    
    $scope.deleteItem = function(userName) {
        var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/delete/'+ userName);
        Vocabulary.remove({}, function(response){
        	if(response.HTTP_CODE == 200){
        		for (var i=VocabularCollection.length-1; i>=0; i--) {
        		    if (VocabularCollection[i].name === userName) {
        		    	VocabularCollection.splice(i, 1);
        		    }
        		}
        		$scope.manageTableParams.reload();
        	}
		}); 
    }
    
    $scope.addVocabToTable = function(vocabName, vocabPrefix, vocabLoc) {
        var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/add');
        Vocabulary.save({name:vocabName, prefix:vocabPrefix, location:vocabLoc}, function(response){
			if(response.HTTP_CODE == 200){
				$scope.switchToManageDialog();
			}
		});        
    }
});