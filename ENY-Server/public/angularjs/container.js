var container = angular.module('container', []);

container.controller('container', function($scope, $http) {

    $http.get("http://10.3.16.163:3000/uicontainerstatus/")
  		.then(function(response) {
      		$scope.items = response.data;
  		});
  
});
