var simulator = angular.module('simulator', []);

simulator.controller('simulator', function($scope, $http) {

    $http.get("http://localhost:3000/containerstatus/" + $scope.uid)
  		.then(function(response) {
      		$scope.info = response.data;
  		});
  
});
