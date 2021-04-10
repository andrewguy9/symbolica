goog.provide('yq');

yq.debugMessage = 'Dead Code';

yq.yayQuery = function() {
    var yay = {};
    yay.sayHello = function(message) {
        console.log(message);
    };
    yay.getMessage = function() {
        return 'Hello, world from Closure library!';
    };
    return yay;
};

