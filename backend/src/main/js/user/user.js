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
        client({ path: '/api/movies',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({movies: response.entity});
        });
    }

    fetchData(){
        let client = rest.wrap(mime);
        client({ path: '/api/movies',
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
