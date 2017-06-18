export class User extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            movies: ''
        };
        this.fetchData = this.fetchData.bind(this);
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/movies',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({movies: response.entity});
        });
    }

    fetchData(){
        let client = rest.wrap(mime);
        client({ path: '/movies',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({movies: response.entity});
        });
    }

    render() {
        const movieList = this.state.movies;
        const arr = movieList instanceof Array ? movieList : [movieList];
        console.log(movieList);
        const movies = arr.map((movie) =>
            <Movie movie={movie} key={movie.id}/>
        );
        const title = (
            <h3>Login</h3>
        );

        return (
            <div>
                <Panel header={title} bsStyle="primary">

                </Panel>
            </div>

        );
    }
}

//
// <script type="text/javascript" src="/webjars/angularjs/angular.min.js"></script>
// <script type="text/javascript">
//     angular.module("app", []).controller("home", function($http) {
//         var self = this;
//         $http.get("/user").success(function(data) {
//             self.user = data.userAuthentication.details.name;
//             self.authenticated = true;
//         }).error(function() {
//             self.user = "N/A";
//             self.authenticated = false;
//         });
//     });
// </script>
//
// <div class="container" ng-show="!home.authenticated">
//     Login with: <a href="/login">Facebook</a>
//     </div>
//     <div class="container" ng-show="home.authenticated">
//     Logged in as: <span ng-bind="home.user"></span>
//     </div>
//