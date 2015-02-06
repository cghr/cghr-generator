
 angular.module('myApp',[])
.controller('mainCtrl',function($scope,$http){

  $scope.projects=[{name:'hc',langs:['']},{name:'mvm',langs:['','mr']},{name:'hcamp',langs:['']}];
  $scope.generate=function(project,langs){

    $scope.processing=true

    $http.post("api/generate/"+project,{langs:langs})
    .success(function(){
      alert('Generated Successfully');
        $scope.processing=false
    })
    .error(function(){
      $scope.processing=false
      alert('Failed ! Contact Ravi Tej');
    });


  };


});