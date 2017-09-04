new Vue({
    el: '#addSuit',
    data() {
        return {
            name: 'default name',
            description: 'default description'
        }
    },
    methods: {
        addSuit: function() {
            this.name = $("#nameSuit").val();
            this.description = $("#descriptionSuit").val();

            axios.post('/addTestSuit', {name: this.name, description: this.description}).then(function(response) {}).catch(function(error) {});
        }
    },
    watch: {
        message: function() {
            console.log(this.message)
        }
    }
});