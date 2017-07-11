/*jshint esversion: 6 */

import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import Table from 'react-bootstrap/lib/Table';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'
import Panel from 'react-bootstrap/lib/Panel';
import ButtonToolbar from 'react-bootstrap/lib/ButtonToolbar';

const rest = require('rest');
const mime = require('rest/interceptor/mime');

import {Movie, ShowMovieEditModal} from './movie';

export class ShowMovieList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            movies: '',
            watchListId: props.watchListId
        };
        this.fetchData = this.fetchData.bind(this);
    }

    componentDidMount() {
        let client = rest.wrap(mime);
        client({ path: '/watchlist/' + this.state.watchListId + '/movies',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({movies: response.entity});
        });
    }

    fetchData(){
        let client = rest.wrap(mime);
        client({ path: '/watchlist/' + this.state.watchListId + '/movies',
            headers: {'Accept': 'application/json'}}).then(response => {
            this.setState({movies: response.entity});
        });
    }

    render() {
        const movieList = this.state.movies;
        const arr = movieList instanceof Array ? movieList : [movieList];
        const movies = arr.map((movie) =>
            <Movie movie={movie} key={movie.id} watchlistId={movie.watchlistId} />
        );
        const title = (
            <h3>Movies</h3>
        );
        const newMovie = {
            id: '',
            name: '',
            studio: '',
            director: '',
            notableActors: '',
            releaseYear: '',
            releaseDate: '',
            genre: '',
            imdbLink: '',
            seen: '',
            cinemaWorthy: '',
            wanted: '',
            watchlistId: ''
        };

        return (
            <div>
                <Panel header={title} bsStyle="primary">
                    <ButtonToolbar>
                        <ShowMovieEditModal movie={newMovie} watchlistId={this.state.watchListId} />
                        <Button bsStyle="info" bsSize="large" onClick={this.fetchData}><Glyphicon glyph="refresh"/></Button>

                    </ButtonToolbar>
                    <Table responsive>
                        <thead >
                            <tr>
                                <th>Name</th>
                                <th>Year of Release</th>
                                <th>Genre</th>
                                <th>Seen</th>
                                <th>Cinema</th>
                                <th>Want</th>
                                <th>IMDB</th>
                                <th><Glyphicon glyph="zoom-in" /></th>
                                <th><Glyphicon glyph="edit" /></th>
                                <th><Glyphicon glyph="trash" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            {movies}
                        </tbody>
                    </Table>
                </Panel>
            </div>

        );
    }
}