/**
 * 
 */
app.controller('SearchController', function ($scope, $http, $resource, ngTableParams) {
	
	var keywordscope;
	
	$scope.search = function(keyword) {
		keywordscope = keyword;
		$scope.searchTableParams.reload();
	};

        		
	$scope.searchTableParams = new ngTableParams({
		page: 1,            
		count: 10           
	}, {
		total: 0, 
		getData: function($defer, params) {
			var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/search/' + keywordscope);
			Vocabulary.get({}, function(response){
				if(response.HTTP_CODE == 200){
					ResultCollection = response.result;
					params.total(ResultCollection.length);
					
					$defer.resolve(ResultCollection.slice((params.page() - 1) * params.count(), params.page() * params.count()));
				}
			});
		}
	});

    
    
    $scope.addResult = function(userName) {
    	this.searchvalue.keyword = userName;
    };
    
    var VocabularCollection;
    
	$scope.switchToManageDialog = function() {	
    	$scope.selection = "manageDialog";
    	$scope.manageTableParams.reload();
	}
	
    $scope.manageTableParams = new ngTableParams({
    	page: 1,            
    	count: 10           
    }, {
    	total: 0, 
    	getData: function($defer, params) {
    		var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/getAllVocabulary');
    		Vocabulary.get({}, function(response){
    			if(response.HTTP_CODE == 200){
    				VocabularCollection = response.result;
    				params.total(VocabularCollection.length);
    				$defer.resolve(VocabularCollection.slice((params.page() - 1) * params.count(), params.page() * params.count()));
    			}
    			else{
    				alert("error");
    			}
    		})
    	}
    });
    
    $scope.switchToSearchDialog = function() {
    	$scope.selection = "searchDialog";
    }
    
    $scope.switchToAddDialog = function() {
    	$scope.selection = "addVocabDialog";
    }
    
    $scope.deleteItem = function(userName) {
        var Vocabulary = $resource('/ManageVocabulary/api/vocabulary/delete/');
        Vocabulary.save({value:userName}, function(response){
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