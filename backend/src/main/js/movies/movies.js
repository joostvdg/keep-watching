/*jshint esversion: 6 */

import React from 'react';
import Button from 'react-bootstrap/lib/Button';
import Modal from 'react-bootstrap/lib/Modal';
import Table from 'react-bootstrap/lib/Table';
import Form from 'react-bootstrap/lib/Form';
import FormControl from 'react-bootstrap/lib/FormControl'

import FormGroup from 'react-bootstrap/lib/FormGroup';
import ControlLabel from 'react-bootstrap/lib/ControlLabel';
import Glyphicon from 'react-bootstrap/lib/Glyphicon'
import Panel from 'react-bootstrap/lib/Panel';
import ButtonToolbar from 'react-bootstrap/lib/ButtonToolbar';

const rest = require('rest');
const mime = require('rest/interceptor/mime');

import {Movie} from './movie';

export class ShowNewMovieModal extends React.Component {
    constructor() {
        super();
        this.state = {
            showModal: false,
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
            wanted: ''
        };
        this.close = this.close.bind(this);
        this.open = this.open.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    close() {
        this.setState({ showModal: false });
    }

    open() {
        this.setState({ showModal: true });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        console.log('A name was submitted: ' + this.state.name);
        fetch('/movies', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: this.state.name,
                studio: this.state.studio,
                director: this.state.director,
                notableActors: this.state.notableActors,
                releaseYear: this.state.releaseYear,
                releaseDate: this.state.releaseDate,
                genre: this.state.genre,
                imdbLink: this.state.imdbLink,
                seen: this.state.seen,
                cinemaWorthy: this.state.cinemaWorthy,
                wanted: this.state.wanted,
            })
        });
        this.state = {
            showModal: false,
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
            wanted: ''
        }
    }

    render() {
        return (
            <div>
                <Button bsStyle="success" onClick={this.open}>
                    New
                </Button>

                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>New Movie</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                       <Form onSubmit={this.handleSubmit}>
                           <FormGroup controlId="newMovieInputName">
                               <ControlLabel>Name</ControlLabel>
                               <FormControl type="text" value={this.state.name} name="name" placeholder="name" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputStudio">
                               <ControlLabel>Studio</ControlLabel>
                               <FormControl type="text" value={this.state.studio} name="studio" placeholder="studio" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputDirector">
                               <ControlLabel>Director</ControlLabel>
                               <FormControl type="text" value={this.state.director} name="director" placeholder="director" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputNotableActors">
                               <ControlLabel>Notable Actions (comma separated list)</ControlLabel>
                               <FormControl type="text" value={this.state.notableActors} name="notableActors" placeholder="notableActors" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputYear">
                               <ControlLabel>Year of release (e.g. 2017)</ControlLabel>
                               <FormControl type="text" value={this.state.releaseYear} name="releaseYear" placeholder="releaseYear" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputGenre">
                               <ControlLabel>Genre's (comma separated list)</ControlLabel>
                               <FormControl type="text" value={this.state.genre} name="genre" placeholder="genre" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputImdb">
                               <ControlLabel>IMDB Link</ControlLabel>
                               <FormControl type="text" value={this.state.imdbLink} name="imdbLink" placeholder="imdbLink" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="">
                               <ControlLabel>Seen</ControlLabel>
                               <FormControl type="checkbox" value={this.state.seen} name="seen" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputCinema">
                               <ControlLabel>Cinema</ControlLabel>
                               <FormControl type="checkbox" value={this.state.cinemaWorthy} name="cinemaWorthy" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>
                           <FormGroup controlId="newMovieInputWanted">
                               <ControlLabel>Want to see</ControlLabel>
                               <FormControl type="checkbox" value={this.state.wanted} name="wanted" onChange={this.handleChange} />
                               <FormControl.Feedback />
                           </FormGroup>

                            <Button type="submit">Create</Button>
                       </Form>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        );
    }
}

export class ShowMovieList extends React.Component {
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
            <h3>Movies</h3>
        );

        return (
            <div>
                <Panel header={title} bsStyle="primary">
                    <ButtonToolbar>
                        <ShowNewMovieModal />
                        <Button bsStyle="info" onClick={this.fetchData}><Glyphicon glyph="refresh"/></Button>

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